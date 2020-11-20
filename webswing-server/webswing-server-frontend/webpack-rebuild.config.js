const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.config.js");
const path = require("path");
const DESTINATION = path.resolve(__dirname, "../webswing-server-war/target/webswing-server/javascript");

/**
 * Webpack Plugins
 */
module.exports = webpackMerge(commonConfig, {
    mode: "development",
    devtool: "eval-cheap-source-map",
    output: {
        path: DESTINATION
    }
});

