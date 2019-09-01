const HtmlWebPackPlugin = require("html-webpack-plugin");
const path = require('path');

module.exports = {
  devServer: {
    contentBase: 'dist',
    proxy: {
      '/results': 'http://localhost:8080'
    }
  },
  module: {
    rules: [
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
    path: path.resolve('dist', 'static'),
    filename: 'main.js',
    publicPath: 'static'
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: "./src/index.html",
      filename: path.resolve('dist', 'index.html'),
    })
  ]
};
