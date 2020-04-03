const path = require("path");
const ROOT = path.resolve(__dirname, "src/test/webapp");
var webpack = require("webpack");

/**
 * Webpack Plugins
 */
const HtmlWebpackPlugin = require("html-webpack-plugin");
const LoaderOptionsPlugin = require("webpack/lib/LoaderOptionsPlugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
module.exports = {
  context: ROOT,

  resolve: {
    extensions: [".ts", ".js"]
  },

  module: {
    rules: [
      {
        test: /\.ts$/,
        exclude: /node_modules/,
        use: {
          loader: "tslint-loader",
          options: {
            emitErrors: true
          }
        },
        enforce: "pre"
      },

      {
        test: /\.ts$/,
        exclude: [/node_modules/],
        use: ["awesome-typescript-loader"]
      },

      {
        test: /\.scss$/,
        use: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: ["css-loader", "sass-loader"],
          publicPath: "../"
        })
      },

      {
        test: /\.(jpg|png|gif)$/,
        use: "file-loader"
      },

      {
        test: /\.(svg|woff|woff2|eot|ttf)$/,
        use: "file-loader?outputPath=fonts/"
      },

      {
        test: /.html$/,
        exclude: /index.html$/,
        use: "html-loader"
      }
    ]
  },

  plugins: [
    new ExtractTextPlugin("css/style.css"),
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
      "window.jQuery": "jquery"
    })
  ],

  entry: "./index.ts"
};
