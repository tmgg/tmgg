/** @type {import('rollup').RollupOptions} */

import babel from 'rollup-plugin-babel'

export default {
    input: 'src/index.js',
    output: {
            file: 'dist/index.es.js',
            format: 'es'
    },
    plugins: [
        //jsx( {factory: 'React.createElement'} )
        babel()
    ],
    sourceMap: true,

   // external: ['react']

};