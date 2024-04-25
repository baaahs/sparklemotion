const path = require('path');

config = config || {};

// cwd is build/js/packages/sparklemotion
// config.resolve.modules.push(path.resolve(__dirname, "../../node_modules"));

// config.resolve.alias = {
//     js: path.resolve(__dirname, "../../../../src/jsMain/js/"),
// };

config.watchOptions = {
    ignored: ['**/*.kt']
};