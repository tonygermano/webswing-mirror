const path = require("path");
const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.config.js");
const ROOT = path.resolve(__dirname, "src/main/webapp");

/**
 * Webpack Plugins
 */

module.exports = webpackMerge(commonConfig, {
  context: ROOT,

  mode: "production",
  output: {
    library: "webswing-directdraw-javascript",
    libraryTarget: "umd" // exposes and know when to use module.exports or exports.
  },
});
