var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") return Reflect.decorate(decorators, target, key, desc);
    switch (arguments.length) {
        case 2: return decorators.reduceRight(function(o, d) { return (d && d(o)) || o; }, target);
        case 3: return decorators.reduceRight(function(o, d) { return (d && d(target, key)), void 0; }, void 0);
        case 4: return decorators.reduceRight(function(o, d) { return (d && d(target, key, o)) || o; }, desc);
    }
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
var angular2_1 = require('angular2/angular2');
var http_1 = require('angular2/http');
var config_1 = require("./config");
var ConfigFormComponent = (function () {
    function ConfigFormComponent(http) {
        // let's feed that with temporary values, until the real values are loaded
        this.resultsLoaded = false;
        // 2. Feed some temporary model so that it does not complain
        this.model = new config_1.Config('2015-11-11T11:01', 1, 2, 3, 4, '2015-11-11T11:02', 5, 6, 7, 8, '2015-11-11T11:03', 9, 10, 11, 12, '2015-11-11T11:04');
        this.submitted = false;
        var self = this;
        this.http = http;
        // 1. Go get actual values from database.
        http.get('/getcurrentkeezerconfig').map(function (res) { return res.json(); }).subscribe(function (config) {
            console.log("Successfully received current config: " + JSON.stringify(config));
            // 3. Load model = new Config(..) with these values.
            self.model = config;
            self.resultsLoaded = true;
        });
    }
    ConfigFormComponent.prototype.onSubmit = function () {
        // 4: Submit this to server, and display errors if any.
        console.log("In onSubmit() method!");
        var self = this;
        this.submitted = true;
        var headers = new http_1.Headers();
        headers.append('Content-Type', 'application/json');
        this.http.post('/updatekeezerconfig', JSON.stringify(this.model), { headers: headers }).map(function (res) { return res.json(); }).subscribe(function (data) {
            console.log("****** Update results: " + JSON.stringify(data));
            if (data.success == true) {
                console.log("Successfully updated result");
            }
            else {
                console.log("There are errors : " + data.errors);
            }
        }, function (err) { return console.log("**** Update results, error. : " + err); }, function () { return console.log("*** Update results... "); });
    };
    ConfigFormComponent = __decorate([
        angular2_1.Component({
            selector: 'config-form',
            templateUrl: 'app/config-form.component.html',
            directives: [angular2_1.CORE_DIRECTIVES, angular2_1.FORM_DIRECTIVES],
            viewProviders: [http_1.HTTP_PROVIDERS]
        }),
        __param(0, angular2_1.Inject(angular2_1.forwardRef(function () { return http_1.Http; })))
    ], ConfigFormComponent);
    return ConfigFormComponent;
})();
exports.ConfigFormComponent = ConfigFormComponent;
//# sourceMappingURL=config-form.component.js.map