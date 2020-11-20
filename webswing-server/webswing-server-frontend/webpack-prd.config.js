const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.config.js");
const CompressionPlugin = require('compression-webpack-plugin');

module.exports = webpackMerge(commonConfig, {
  mode: "production",
  optimization: {
    minimize: true
  },
  plugins: [new CompressionPlugin()]

});
