import {FormControl, ValidatorFn} from "@angular/forms";

export class ValidatorUtils {


  public static validateMultipleSelect(aMinSelected: number, aMaxSelected: number):
    ValidatorFn {
    return (aControl: FormControl) => {

      if (aControl.value == null) {
        // The user has not selected any values -- so assume everything is valid
        return null;
      }

      let totalValuesSelected: number = aControl.value.length;
      if (totalValuesSelected < aMinSelected) {
        return {'custom_error': `You must select at least ${aMinSelected} entries.`};
      }
      else if (totalValuesSelected > aMaxSelected) {
        return {'custom_error': `You must select at most ${aMaxSelected} entries.`};
      }
      else {
        // The user selected a valid number of values -- so no error is returned
        return null;
      }

    }
  }  // end of static method

}
