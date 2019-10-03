const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.config.js");

/**
 * Webpack Plugins
 */
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");

module.exports = webpackMerge(commonConfig, {
  mode: "production",
  // plugins: [new UglifyJsPlugin()]
});
