/** @type {import('tailwindcss').Config} */
module.exports = {

  content: [
  "../resources/static/css/templates/**/*.{html,js}"
  ],
  theme: {
    extend: {},
  },
  plugins: [require('@tailwindcss/aspect-ratio'),
  ],

   theme: {
      fontFamily: {
        'sans': ['ui-sans-serif', 'system-ui', ...],
        'serif': ['ui-serif', 'Georgia', ...],
        'mono': ['ui-monospace', 'SFMono-Regular', ...],
        'display': ['Oswald', ...],
        'body': ['"Open Sans"', ...],
      }
    }
}


