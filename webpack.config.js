const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: [
        'react-hot-loader/patch',
        './index.jsx'
    ],
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader'],
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            }
        ],
    },
    resolve: {
        extensions: ['*', '.js', '.jsx']
    },
    output: {
        path: __dirname + '/build/js/',
        publicPath: '/js/',
        filename: 'app.js'
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
        open: true // Opens page in browser
    }
};
