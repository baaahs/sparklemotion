const path = require('path');

config = config || {};

// cwd is build/js/packages/sparklemotion
config.resolve.modules.push(path.resolve(__dirname, "../../node_modules"));

config.module.rules.push(
    {
        test: /\.(js|jsx)$/,
        include: /src\/jsMain\/js/,
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
        test: /\.(css|sass|scss)$/,
        include: /src\/jsMain\/js/,
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
    config.devServer = config.devServer || {};

    // config.devServer.noInfo = false

    // config.devServer.hot = true;
    // config.devServer.watchOptions = {
    //   aggregateTimeout: 2000,
    //   poll: 1000
    // };

    // // see https://discuss.kotlinlang.org/t/kotlin-js-react-unstable-building/15582/6
    // config.entry.main = config.entry.main.map(
    //   s => s.replace(`/kotlin-out/`, "/"),
    // );

    // config.optimization = {
        // splitChunks: true
    //     splitChunks: {
    //         chunks: 'all',
    //         name: true,
    //
    //         cacheGroups: {
    //             commons: {
    //                 test: /[\\/]node_modules[\\/]|[\\/]packages_imported[\\/]|[\\/]kotlin(-dce(-dev)?)?[\\/]kotlin/,
    //                 name: 'vendors',
    //                 chunks: 'all'
    //             },
    //             default: {
    //                 minChunks: 2,
    //                 priority: -20,
    //                 reuseExistingChunk: true
    //             }
    //         }
    //     }
    // };
} else {
    // Otherwise we get "unknown module and require" from production build.
    // config.optimization = {
    //     sideEffects: false,
        // splitChunks: {
        //     chunks: function(chunk) {
        //         return false;
        //     }
        // }
    // };
}

// config.devtool = 'eval';

// config.plugins = [
//     new webpack.SourceMapDevToolPlugin({
//         test: /\.(js|jsx|css|sass|scss)$/,
//         exclude: /node_modules/,
//     }),
// ];
