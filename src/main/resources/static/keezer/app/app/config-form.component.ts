import {Component, CORE_DIRECTIVES, FORM_DIRECTIVES} from 'angular2/angular2';
import {Config} from "./config";

@Component({
  selector: 'config-form',
  templateUrl: 'app/config-form.component.html',
  directives: [CORE_DIRECTIVES, FORM_DIRECTIVES]
})
export class ConfigFormComponent {

  // TODO: 1. Go get actual values from database.



  // TODO: 2. Load model = new Config(..) with these values.


  model = new Config('2015-11-11 08:00:00', 18, 19, 20, 21, 'test', 22, 23, 24, 25, 'test2', 0, 1, 2, 3, 'test3');

  submitted = false;

  onSubmit() {

    // TODO 3: Submit this to server, and display errors if any.
    console.log("In onSubmit() method!");

    this.submitted = true;  }

  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }
}
