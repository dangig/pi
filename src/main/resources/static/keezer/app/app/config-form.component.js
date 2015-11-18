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
        // TODO: 1. Go get actual values from database.
        //http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(config => this.config = config);
        //this.http.get('/getcurrentkeezerconfig').map(res => res.json()).subscribe(function (config) {
        ///  console.log("Successfully received current config: " + config)
        //});
        // TODO: 2. Load model = new Config(..) with these values.
        // let's feed that with temporary values, until the real values are loaded
        this.resultsLoaded = false;
        this.model = new config_1.Config('2015-11-11T11:01', 1, 2, 3, 4, '2015-11-11T11:02', 5, 6, 7, 8, '2015-11-11T11:03', 9, 10, 11, 12, '2015-11-11T11:04');
        this.submitted = false;
        var self = this;
        http.get('/getcurrentkeezerconfig').map(function (res) { return res.json(); }).subscribe(function (config) {
            console.log("Successfully received current config: " + JSON.stringify(config));
            //this.model = config;
            self.model = config; //new Config('2015-11-11T11:00', 2224, 19, 20, 21, '2015-11-20T20:00', 22, 23, 24, 25, '2015-11-21T21:00', 0, 1, 2, 3, '2015-11-22T22:00');
            self.resultsLoaded = true;
        });
    }
    ConfigFormComponent.prototype.onSubmit = function () {
        // TODO 3: Submit this to server, and display errors if any.
        console.log("In onSubmit() method!");
        this.submitted = true;
    };
    Object.defineProperty(ConfigFormComponent.prototype, "diagnostic", {
        // TODO: Remove this when we're done
        get: function () { return JSON.stringify(this.model); },
        enumerable: true,
        configurable: true
    });
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