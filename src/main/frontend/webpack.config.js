const HtmlWebpackPlugin = require("html-webpack-plugin");
const path = require('path');

module.exports = {
  devServer: {
    port: 5001,
    contentBase: 'dist',
    proxy: {
      '/results': 'http://localhost:5000'
    }
  },
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [
          'style-loader',
          'css-loader'
        ],
      },
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader"
        }
      },
      {
        test: /\.html$/,
        use: [
          {
            loader: "html-loader"
          }
        ]
      }
    ]
  },
  entry: './src/index.js',
  output: {
    filename: 'main.js',
    publicPath: 'static'
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "./src/index.html",
      filename: "index.html",
      favicon: "./src/favicon.ico"
    })
  ]
};
