const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.config.js");

module.exports = webpackMerge(commonConfig, {
  mode: "development",
  devtool: "eval-cheap-source-map" //comment this for IE11
});
