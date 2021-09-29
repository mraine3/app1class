package com.lessons.services;

import com.lessons.models.grid.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("com.lessons.services.GridService")
public class GridService {
    private static final Logger logger = LoggerFactory.getLogger(GridService.class);

    @Resource
    private ElasticSearchService elasticSearchService;


    /**
     *  1. Run a search
     *  2. Put the results into a list
     *  3. Create a GridGetRowsResponseDTO object
     *  4. Return the GridGetRowsResponseDTO object
     *
     * @param aGridRequestDTO holds information about the request
     * @return holds the response object (that holds the list of data, p
     */
    public GridGetRowsResponseDTO getPageOfData(String aIndexName, List<String> aFieldsToSearch, GridGetRowsRequestDTO aGridRequestDTO) throws Exception {

        logger.debug("getPageOfData()  startRow={}   endRow={}", aGridRequestDTO.getStartRow(), aGridRequestDTO.getEndRow() );

        // Calculate the page size  (to determine how many records to request from ES)
        int pageSize = aGridRequestDTO.getEndRow() - aGridRequestDTO.getStartRow();

        // Build the search_after clause  (which is used to get page 2, page 3, page 4 from an ES query)
        String esSearchAfterClause = "";
        if (aGridRequestDTO.getStartRow() > 0) {
            // Getting the 2nd, 3rd, 4th page....
            esSearchAfterClause = " \"search_after\": [" + aGridRequestDTO.getSearchAfterClause() + "],";
        }

        // Construct the *SORT* clause
        String esSortClauseWithComma = generateSortClauseFromSortParams(aGridRequestDTO.getSortModel() );

        // Construct the *FILTER* clause (if any)
        String filterClause = generateFilterClause(aGridRequestDTO.getFilterModel() );


        // Construct the *QUERY* clause
        String cleanedQuery = this.elasticSearchService.cleanupQuery( aGridRequestDTO.getRawSearchQuery()  );
        String queryStringClause = generateQueryStringClause(cleanedQuery, aFieldsToSearch );


        // Assemble the pieces to build the JSON query
        String jsonQuery;
        if (StringUtils.isNotEmpty(filterClause)) {
            // This ES query has a filter
            jsonQuery = "{\n" +
                    esSearchAfterClause + "\n" +
                    esSortClauseWithComma + "\n" +
                    "   \"track_total_hits\": true,\n" +
                    "   \"size\": " + pageSize +",\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": {\n" +
                    queryStringClause + "\n" +
                    "      },\n" +
                    filterClause +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }
        else {
            // This ES query does *NOT* have a filter
            jsonQuery = "{\n" +
                    esSearchAfterClause + "\n" +
                    esSortClauseWithComma + "\n" +
                    "   \"track_total_hits\": true,\n" +
                    "   \"size\": " + pageSize +",\n" +
                    "   \"query\": { \n" +
                    queryStringClause + "\n" +
                    "}\n" +
                    "}";
        }

        boolean isValidQuery = true;
        if ((StringUtils.isNotEmpty(cleanedQuery)) && (aGridRequestDTO.getStartRow() == 0)) {
            String fullQueryClause = "{ \"query\": { \n" + queryStringClause + "\n" + "}}\n";

            // The user entered a search query and this is the first page -- so validate it
            isValidQuery = this.elasticSearchService.isQueryValid(aIndexName, fullQueryClause);
        }

        if (! isValidQuery) {
            // The query is not valid.  So, return a GridGetRowsResponseDTO object with the isValidQuery=false
            GridGetRowsResponseDTO responseDTO = new GridGetRowsResponseDTO(null, 0, null);
            responseDTO.setIsValidQuery(false);
            return responseDTO;
        }


        // Execute the search and generate a GetResponsRowsResponseDTO object
        // -- This sets responseDTO.setData() and responseDTo.setTotalMatches()
        GridGetRowsResponseDTO responseDTO  = this.elasticSearchService.runSearchGetRowsResponseDTO(aIndexName, jsonQuery, aGridRequestDTO.getSortModel() );


        // Set the lastRow  (so the ag-grid's infinite scrolling works correctly)
        if (aGridRequestDTO.getEndRow() < responseDTO.getTotalMatches() ) {
            // This is not the last page.  So, set lastRow=-1  (which turns on infinite scrolling)
            responseDTO.setLastRow(-1);
        }
        else {
            // This is the last page.  So, set lastRow=totalMatches (which turns off infinite scrolling)
            responseDTO.setLastRow( responseDTO.getTotalMatches() );
        }

        return responseDTO;
    }

    private String generateQueryStringClause(String aCleaneQuery, List<String> aFieldsToSearch) {
        String queryStringClause;
        String fieldsClause = "";

        if (CollectionUtils.isNotEmpty(aFieldsToSearch)) {
            // A list of fields were passed-in.  So, generate a string that holds "fields": ["field1", "field2", "field3"],
            fieldsClause = "\"fields\": [\"" + StringUtils.join(aFieldsToSearch, "\",\"") + "\"],\n";
        }

        // Get the cleaned query
        String cleanedQuery = this.elasticSearchService.cleanupQuery(aCleaneQuery );

        if (StringUtils.isEmpty(cleanedQuery)) {
            // There is no query.  So, use ElasticSearch's match_all to run a search with no query
            queryStringClause = "  \"match_all\": {}\n";
        }
        else {
            // There is a query, so return a query_string clause
            queryStringClause = "  \"query_string\": {\n" +
                    fieldsClause +
                    "    \"query\": \"" + cleanedQuery + "\"\n" +
                    "     }\n";
        }

        return queryStringClause;
    }



    /**
     * Generate an ElasticSearch Filter clause
     *
     *       "filter": [
     *         {
     *           "term" : {
     *             "id":254
     *           }
     *         },
     *         {
     *           "term" : {
     *             "cityId":35
     *           }
     *         }
     *       ],
     *
     * @param aFilterModelsMap holds a map of filter objects
     * @return String containing a filter clause
     */
    private String generateFilterClause(Map<String, ColumnFilter> aFilterModelsMap) {
        if ((aFilterModelsMap == null) || (aFilterModelsMap.size() == 0)){
            // There are no filters
            return null;
        }

        // Start off the filter ES query
        StringBuilder sbFilterClause = new StringBuilder("\"filter\": [");

        for (Map.Entry<String, ColumnFilter> filter: aFilterModelsMap.entrySet() ) {
            String originalFieldName = filter.getKey();
            String actualFilterFieldName;

            // Get the columnFilter object -- it may be a NumericColumnFilter or a TextColumnFilter
            ColumnFilter columnFilter = filter.getValue();

            if (columnFilter instanceof NumberColumnFilter) {
                // This is a numeric filter.

                // The actual ES field to search will have .filtered on it
                actualFilterFieldName = originalFieldName + ".filtered";

                NumberColumnFilter numberColumnFilter = (NumberColumnFilter) filter.getValue();
                Integer filterValue = numberColumnFilter.getFilter();

                // Add the filter to the ES query
                sbFilterClause.append("{" +
                        "                \"term\" : {\n" +
                        "                  \"" + actualFilterFieldName + "\":\"" + filterValue +"\"\n" +
                        "                }\n" +
                        "              },");
            }
            else if (columnFilter instanceof TextColumnFilter) {
                // This is a text filter

                // TODO: Get the correct fieldname by examining ElasticSearch at startup

                // The actual ES field to search will have .filtered on it
                actualFilterFieldName = originalFieldName + ".filtered";

                TextColumnFilter textColumnFilter = (TextColumnFilter) filter.getValue();
                String filterValue = textColumnFilter.getFilter();

                // Add the filter to the ES query
                // NOTE: Set the filterValue to lowercase (as the filtered collumn is stored as lowercase)
                sbFilterClause.append("{" +
                        "                \"term\" : {\n" +
                        "                  \"" + actualFilterFieldName + "\":\"" + filterValue.toLowerCase() +"\"\n" +
                        "                }\n" +
                        "              },");
            }
            else {
                throw new RuntimeException("Error in generateFilterClause():  Unknown filter Type.");
            }

        }  // End of looping through filters

        // Remove the last comma
        sbFilterClause.deleteCharAt(sbFilterClause.length() - 1);

        // Add the closing square bracket
        sbFilterClause.append("]\n");

        return sbFilterClause.toString();
    }



    private String generateSortClauseFromSortParams(List<SortModel> aSortModelList) {

        StringBuilder sbSortClause = new StringBuilder("\"sort\": [\n");

        // Loop through the list of sort models, generating the ES sort clause
        for (SortModel sortModel: aSortModelList) {
            String sortFieldName = sortModel.getColId();
            String sortOrder = sortModel.getSort();

            if (sortFieldName.equalsIgnoreCase("_score")) {
                // We are sorting by the _score so do not include the missing: field
                sbSortClause.append(
                        "{\n" +
                                "\"" + sortFieldName + "\": {\n" +
                                "            \"order\": \"" + sortOrder + "\"\n" +
                                "          }\n" +
                                "        },");
            }
            else {
                // We are sorting by a non _score field.  So, include the missing field
                sbSortClause.append(
                        "{\n" +
                                "\"" + sortFieldName + "\": {\n" +
                                "            \"order\": \"" + sortOrder + "\",\n" +
                                "            \"missing\" : \"_first\"\n" +
                                "          }\n" +
                                "        },");
            }

        }

        // Remove the last comma
        sbSortClause.deleteCharAt(sbSortClause.length() - 1);

        // Add the closing square bracket and comma to the end
        sbSortClause.append("],");

        return sbSortClause.toString();
    }
}
