const path = require('path');
const webpack = require('webpack');

module.exports = {
  entry: ['react-hot-loader/patch', './src/jsMain/js/index.jsx'],
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
          // 'sass-loader', // Removed because of https://github.com/sass/node-sass/issues/2625
        ],
      },
    ],
  },
  resolve: {
    extensions: ['*', '.js', '.jsx'],
  },
  output: {
    path: __dirname + '/build/webpack/',
    publicPath: '/js/',
    filename: 'react_app.js',
  },
  devtool: false,
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.SourceMapDevToolPlugin({
      test: /\.(js|jsx|css|sass|scss)$/,
      exclude: /node_modules/,
    }),
  ],
  devServer: {
    contentBase: './',
    hot: true,
    port: 8000,
    open: true, // Opens page in browser
  },
};
