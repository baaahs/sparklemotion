// ugh — https://github.com/puppeteer/puppeteer/issues/3774

config.set({
    browsers: ['ChromeHeadlessFixed'],

    customLaunchers: {
        'ChromeHeadlessFixed': {
            'base': 'ChromeHeadless',
            'flags': [
                '--disable-web-security',
                '--disable-site-isolation-trials',
                '--disable-features=VizDisplayCompositor']
        }
    }
});