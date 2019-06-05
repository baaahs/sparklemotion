const path = require('path');
const webpack = require('webpack');

module.exports = {
  entry: ['./src/jsMain/js/index.jsx'],
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      },
      {
        test: /\.(sass|scss)$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
              modules: true,
              localIdentName: '[local]___[hash:base64:5]',
            },
          },
          'sass-loader',
        ],
      },
    ],
  },
  resolve: {
    extensions: ['*', '.js', '.jsx'],
  },
  devtool: 'eval',
  plugins: [
    new webpack.SourceMapDevToolPlugin({
      test: /\.(js|jsx|css|sass|scss)$/,
      exclude: /node_modules/,
    }),
  ],
  devServer: {
    hot: true,
    port: 8000,
    open: true, // Opens page in browser
    contentBase: __dirname + '/build/processedResources/js/main/',
  },
  output: {
    path: __dirname + '/build/processedResources/js/main/',
    filename: 'react_app.js',
  },
};
