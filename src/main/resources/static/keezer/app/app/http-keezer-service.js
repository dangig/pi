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
var http_1 = require('angular2/http');
var angular2_1 = require('angular2/angular2');
var HttpKeezerService = (function () {
    function HttpKeezerService(http) {
        this.http = http;
    }
    HttpKeezerService.prototype.getCurrentConfig = function () {
        var _this = this;
        console.log("*** In HttpKeezerService.getCurrentConfig()");
        return new Promise(function (resolve, reject) {
            _this.http.get('/getcurrentkeezerconfig')
                .map(function (res) { return res.text(); })
                .subscribe(function (data) { return resolve(data); }, function (err) { return _this.logError(err); }, function () { return console.log("Random Quote Complete"); });
        });
    };
    HttpKeezerService = __decorate([
        __param(0, angular2_1.Inject(http_1.Http))
    ], HttpKeezerService);
    return HttpKeezerService;
})();
exports.HttpKeezerService = HttpKeezerService;
//# sourceMappingURL=http-keezer-service.js.map