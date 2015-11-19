import {Component, Inject, forwardRef, CORE_DIRECTIVES, FORM_DIRECTIVES} from 'angular2/angular2';
import {Http, Headers, HTTP_PROVIDERS} from 'angular2/http';
import {Config} from "./config";


@Component({
  selector: 'config-form',
  templateUrl: 'app/config-form.component.html',
  directives: [CORE_DIRECTIVES, FORM_DIRECTIVES],
  viewProviders: [HTTP_PROVIDERS]
})
export class ConfigFormComponent {

  http : Http;

  constructor(@Inject(forwardRef(() => Http)) http) {

    let self = this;
    this.http = http;

    // 1. Go get actual values from database.
    http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(function (config) {
      console.log("Successfully received current config: " + JSON.stringify(config));
      // 3. Load model = new Config(..) with these values.
      self.model = config;
      self.resultsLoaded = true;
    });


  }


  // let's feed that with temporary values, until the real values are loaded

  resultsLoaded = false;

  // 2. Feed some temporary model so that it does not complain
  model = new Config('2015-11-11T11:01', 1, 2, 3, 4, '2015-11-11T11:02', 5, 6, 7, 8, '2015-11-11T11:03', 9, 10, 11, 12, '2015-11-11T11:04');
  submitted = false;
  updateCompletedSuccessfully = false;
  updateCompletedWithErrors = false;
  updateErrors = null;


  onSubmit() {

    // 4: Submit this to server, and display errors if any.
    console.log("In onSubmit() method!");

    // 5: Reset fields following from a previous submit
    this.updateCompletedSuccessfully = false;
    this.updateCompletedWithErrors = false;
    this.updateErrors = null;

    let self = this;
    this.submitted = true;

    var headers = new Headers();
    headers.append('Content-Type', 'application/json');

    this.http.post('/updatekeezerconfig', JSON.stringify(this.model), {headers: headers}).map(res => res.json()).subscribe(

      data => {
        console.log("****** Update results: " + JSON.stringify(data));
        if (data.success == true) {
          console.log("Successfully updated result");
          self.updateCompletedSuccessfully = true;
        } else {
          console.log("There are errors : " + data.errors);
          self.updateCompletedWithErrors = true;
          self.updateErrors = data.errors;
        }

      },
      err => console.log("**** Update results, error. : " + err),
      () => console.log("*** Update results... ")

    );


  }

  // TODO: Remove this when we're done
  //get diagnostic() { return JSON.stringify(this.model); }

}
