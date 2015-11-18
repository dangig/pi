import {Component, Inject, forwardRef, CORE_DIRECTIVES, FORM_DIRECTIVES} from 'angular2/angular2';
import {Http, HTTP_PROVIDERS} from 'angular2/http';
import {Config} from "./config";


@Component({
  selector: 'config-form',
  templateUrl: 'app/config-form.component.html',
  directives: [CORE_DIRECTIVES, FORM_DIRECTIVES],
  viewProviders: [HTTP_PROVIDERS]
})
export class ConfigFormComponent {

  constructor(@Inject(forwardRef(() => Http)) http) {

    let self = this;

    http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(function (config) {
      console.log("Successfully received current config: " + JSON.stringify(config));
      //this.model = config;
      self.model = config; //new Config('2015-11-11T11:00', 2224, 19, 20, 21, '2015-11-20T20:00', 22, 23, 24, 25, '2015-11-21T21:00', 0, 1, 2, 3, '2015-11-22T22:00');
      self.resultsLoaded = true;
    });
  }

  // TODO: 1. Go get actual values from database.
  //http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(config => this.config = config);
  //this.http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(function (config) {
  ///  console.log("Successfully received current config: " + config)
  //});


  // TODO: 2. Load model = new Config(..) with these values.

  // let's feed that with temporary values, until the real values are loaded

  resultsLoaded = false;

  model = new Config('2015-11-11T11:01', 1, 2, 3, 4, '2015-11-11T11:02', 5, 6, 7, 8, '2015-11-11T11:03', 9, 10, 11, 12, '2015-11-11T11:04');

  submitted = false;

  onSubmit() {

    // TODO 3: Submit this to server, and display errors if any.
    console.log("In onSubmit() method!");

    this.submitted = true;  }

  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }

}
