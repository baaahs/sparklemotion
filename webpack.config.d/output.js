var path = require('path');

// cwd is build/js/packages/sparklemotion
config.resolve.modules.push(path.resolve(__dirname, "../../node_modules"));

config.module.rules.push(
    {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
            loader: 'babel-loader',
            options: {
                // includePaths: ['build/js/node_modules'],
                presets: [
                    "@babel/preset-env",
                    "@babel/preset-react",
                ],
                plugins: [
                    // "react-hot-loader/babel",
                    "@babel/plugin-proposal-object-rest-spread",
                    "@babel/plugin-proposal-class-properties",
                ]
            }
        }
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
    }
);

config.resolve.extensions = ['*', '.js', '.jsx'];
config.resolve.alias = {
    js: path.resolve(__dirname, "../../../../src/jsMain/js/"),
};

if (config.devServer) {
    config.devServer.hot = true;
}

// config.devtool = 'eval';

// config.plugins = [
//     new webpack.SourceMapDevToolPlugin({
//         test: /\.(js|jsx|css|sass|scss)$/,
//         exclude: /node_modules/,
//     }),
// ];
