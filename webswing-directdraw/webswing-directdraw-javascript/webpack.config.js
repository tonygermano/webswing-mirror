var webpack = require("webpack");

/**
 * Webpack Plugins
 */
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {

  resolve: {
    extensions: [".ts", ".js"]
  },

  module: {
    rules: [
      {
        test: /\.(ts|js)x?$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },

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
        test: /\.(jpg|png|gif)$/,
        use: "file-loader"
      },

      {
        test: /.html$/,
        exclude: /index.html$/,
        use: "html-loader"
      }
    ]
  },

  plugins: [
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
      "window.jQuery": "jquery"
    }),
    new CopyPlugin([
        { from: 'proto/dd.d.ts', to: 'main/webapp/proto/dd.d.ts' },
    ]),
  ],

  entry: ["./index.ts"]
};
