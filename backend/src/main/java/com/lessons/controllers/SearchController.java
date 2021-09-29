package com.lessons.controllers;

import com.lessons.models.AutoCompleteDTO;
import com.lessons.models.AutoCompleteMatchDTO;
import com.lessons.services.ElasticSearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

@Controller("com.lessons.controllers.SearchController")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Resource
    private ElasticSearchService elasticSearchService;

    /**
     * REST endpoint /api/search/autocomplete
     * @return a list of AutoCopmleteMatchDTO objects (or an empty list if no matches were found)
     * @throws Exception if something bad happens
     */
    @RequestMapping(value = "/api/search/autocomplete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> runAutoComplete(@RequestBody AutoCompleteDTO aAutoCompleteDTO) throws Exception {

        logger.debug("runAutoComplete() started.");

        if (StringUtils.isEmpty(aAutoCompleteDTO.getIndexName() )) {
            // The passed-in DTO does not have a valid index_name
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The index name is empty or null.");
        }
        else if (StringUtils.isEmpty(aAutoCompleteDTO.getReturnedField() )) {
            // The passed-in DTO does not have a valid returned_field
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The returned field is empty or null.");
        }
        else if (StringUtils.isEmpty(aAutoCompleteDTO.getSearchedField() )) {
            // The passed-in DTO does not have a valid searched_field
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The searched field is empty or null.");
        }
        else if (aAutoCompleteDTO.getSize() <= 0) {
            // The passed-in DTO has an invalid size:  It must be positive and less than 100
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The size must be a positive number less than or equal to 100");
        }
        else if (aAutoCompleteDTO.getSize() > 100) {
            // The passed-in DTO has an invalid size:  It must be positive and less than 100
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The size must be a positive number less than or equal to 100");
        }

        // Run the search and get a list of matching AutoCompleteMatchDTO objects
        List<AutoCompleteMatchDTO> matches = elasticSearchService.runAutoComplete(aAutoCompleteDTO);

        // Return a list of matches (or an empty list if there are no matches)
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(matches);
    }

}
