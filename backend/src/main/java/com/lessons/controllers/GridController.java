package com.lessons.controllers;

import com.lessons.models.grid.GridGetRowsRequestDTO;
import com.lessons.models.grid.GridGetRowsResponseDTO;
import com.lessons.models.grid.SortModel;
import com.lessons.services.GridService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller("com.lessons.controllers.GridController")
public class GridController {
    private static final Logger logger = LoggerFactory.getLogger(GridController.class);

    @Resource
    private GridService gridService;


    /**
     * Set default sorting
     *  1) If the sorting model is not empty, then do nothing
     *  2) If the sorting model to empty and rawSearchQuery is empty, then sort by "id" ascending
     *  3) If the sorting model is empty and rawSearchQuery is not empty, then sort by "_score" descending
     *
     * @param aGridRequestDTO holds information about the grid request
     */
    private void setDefaultSorting(GridGetRowsRequestDTO aGridRequestDTO) {
        if (CollectionUtils.isNotEmpty( aGridRequestDTO.getSortModel() )) {
            // Sorting model is not empty.  So, we have sort parameters to use
            return;
        }

        List<SortModel> sortModelList;

        // The sorting model is empty
        if (StringUtils.isBlank(aGridRequestDTO.getRawSearchQuery())) {
            // The sorting model is empty and rawSearchQuery is blank
            // -- User is *not* running a search.  So, sort by "id" ascending
            SortModel sortById = new SortModel("id", "asc");
            sortModelList = Collections.singletonList(sortById);
        }
        else {
            // The sorting mode lis empty and rawSearchQuery is not empty
            // -- User is running a search.  SO, sort by "_score" descending *AND* by "id"
            //    NOTE:  When using the search_after techqique to get the next page, we need to sort by _score *AND* id
            SortModel sortByScore = new SortModel("_score", "desc");
            SortModel sortById = new SortModel("id", "asc");
            sortModelList = Arrays.asList(sortByScore, sortById);
        }


        aGridRequestDTO.setSortModel(sortModelList);
    }


    private void changeSortFieldToUseElasticFieldsForSorting(GridGetRowsRequestDTO aGridRequestDTO) {

        if (CollectionUtils.isNotEmpty(aGridRequestDTO.getSortModel())) {
            for (SortModel sortModel: aGridRequestDTO.getSortModel() ) {
                String sortFieldName = sortModel.getColId();
                if (! sortFieldName.equalsIgnoreCase("id")) {
                    sortFieldName = sortFieldName + ".sort";
                    sortModel.setColId(sortFieldName);
                }
            }
        }
    }


    /**
     * The AG-Grid calls this REST endpoint to load the grid in server-side mode
     *
     * @param aGridRequestDTO holds the Request information
     * @return ResponseEntity that holds a GridGetRowsResponseDTO object
     * @throws Exception if something bad happens
     */
    @RequestMapping(value = "/api/grid/getRows", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> getRows(@RequestBody GridGetRowsRequestDTO aGridRequestDTO) throws Exception {
        logger.debug("getRows() started.");

        // Change the sort field from "priority" to "priority.sort"  (so the sort is case insensitive -- see the mapping)
        changeSortFieldToUseElasticFieldsForSorting(aGridRequestDTO);

        // Set Default sorting
        //  1) If the sorting model is not empty, then do nothing
        //  2) If the sorting model to empty and rawSearchQuery is empty, then sort by "id" ascending
        //  3) If the sorting model is empty and rawSearchQuery is not empty, then sort by "_score" descending
        setDefaultSorting(aGridRequestDTO);

        // Create an array of ES fields to search
        List<String> esFieldsToSearch = Arrays.asList("id.sort", "description", "display_name.sort", "priority.sort");

        // Invoke the GridService to run a search
        GridGetRowsResponseDTO responseDTO = gridService.getPageOfData("reports", esFieldsToSearch, aGridRequestDTO);

        // Return the responseDTO object and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDTO);
    }

}
