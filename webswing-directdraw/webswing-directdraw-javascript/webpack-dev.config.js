const path = require("path");
const webpackMerge = require("webpack-merge");
const ROOT = path.resolve(__dirname, "src/test/webapp");

const commonConfig = require("./webpack.config.js");
const DESTINATION = path.resolve(__dirname, ".tmp");
const HtmlWebpackPlugin = require("html-webpack-plugin");
module.exports = webpackMerge(commonConfig, {
  context: ROOT,

  devtool: "cheap-module-source-map",
  mode: "development",
  devServer: {
    contentBase: path.join(__dirname, ".tmp"),
    compress: true,
    port: 9000,
  },

  output: {
    path: DESTINATION,
    filename: "js/index.js"
  },
  plugins: [new HtmlWebpackPlugin({
    template: "index.html",
    inject: true,
    templateParameters: {
      __WEBSWING_URL: ".."
    }
  }),]
});
