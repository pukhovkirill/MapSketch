import {viteStaticCopy as copy} from 'vite-plugin-static-copy';
import {defineConfig} from 'vite';

export default defineConfig({
  plugins: [
    copy({
      targets: [
        {src: 'data/*', dest: 'data'},
        {src: 'favicon.ico', dest: ''},
      ],
    }),
  ],
  base: './',
  build: {
    sourcemap: true,
  },
});
