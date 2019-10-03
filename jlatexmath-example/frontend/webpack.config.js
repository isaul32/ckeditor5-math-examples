module.exports = {
    devServer: {
		watchContentBase: true,
		proxy: {
			'/api': {
				target: {
					host: '0.0.0.0',
					protocol: 'http:',
					port: 9000
				},
				pathRewrite: {
					'^/api': ''
				}
			}
		}
	}
}
