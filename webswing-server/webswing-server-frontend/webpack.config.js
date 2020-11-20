const path = require("path");
const ROOT = path.resolve(__dirname, "src/main/webapp/javascript");
var webpack = require("webpack");
const DESTINATION = path.resolve(__dirname, "target/webswing-server-frontend/javascript");
const TARGET = path.resolve(__dirname, "target/webswing-server-frontend");
const NODE_PATH = path.resolve(__dirname, "node_modules");

/**
 * Webpack Plugins
 */
const HtmlWebpackPlugin = require("html-webpack-plugin");
const LoaderOptionsPlugin = require("webpack/lib/LoaderOptionsPlugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
  context: ROOT,

  resolve: {
    extensions: [".ts", ".js"]
  },
  output: {
    path: DESTINATION,
    filename: "[name].js"
  },

  module: {
    rules: [
      {
        test: /\.(ts|js)x?$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },

      {
        test: /\.scss$/,
        use: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: ["css-loader", "sass-loader"]
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
    new LoaderOptionsPlugin({
      debug: true,
      options: {
        tslint: {
          configuration: require("./tslint.json"),
          typeCheck: true
        }
      }
    }),
    new ExtractTextPlugin("../css/style.css"),
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
      "window.jQuery": "jquery"
    }),
    new CopyPlugin([
        { from: NODE_PATH + '/pdfjs-dist-viewer-min/build/minified/build', to: TARGET + '/print/build' },
        { from: NODE_PATH + '/pdfjs-dist-viewer-min/build/minified/web', to: TARGET + '/print/web', ignore: ['**/*.pdf', '**/locale/**'] },
        { from: NODE_PATH + '/pdfjs-dist-viewer-min/build/minified/web/locale/en-US', to: TARGET + '/print/web/locale/en-US' }
    ]),
  ],

  entry: {
    "webswing-selector": "./webswing-selector.ts",
    "webswing-security": "./webswing-security.ts",
    "webswing-embed": "./webswing-embed.ts"
  }
};
