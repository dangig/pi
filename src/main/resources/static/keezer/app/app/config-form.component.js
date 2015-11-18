var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") return Reflect.decorate(decorators, target, key, desc);
    switch (arguments.length) {
        case 2: return decorators.reduceRight(function(o, d) { return (d && d(o)) || o; }, target);
        case 3: return decorators.reduceRight(function(o, d) { return (d && d(target, key)), void 0; }, void 0);
        case 4: return decorators.reduceRight(function(o, d) { return (d && d(target, key, o)) || o; }, desc);
    }
};
var angular2_1 = require('angular2/angular2');
var config_1 = require("./config");
var ConfigFormComponent = (function () {
    function ConfigFormComponent() {
        // TODO: 1. Go get actual values from database.
        // TODO: 2. Load model = new Config(..) with these values.
        this.model = new config_1.Config('2015-11-11 08:00:00', 18, 19, 20, 21, 'test', 22, 23, 24, 25, 'test2', 0, 1, 2, 3, 'test3');
        this.submitted = false;
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
            directives: [angular2_1.CORE_DIRECTIVES, angular2_1.FORM_DIRECTIVES]
        })
    ], ConfigFormComponent);
    return ConfigFormComponent;
})();
exports.ConfigFormComponent = ConfigFormComponent;
//# sourceMappingURL=config-form.component.js.map