module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/strongly-recommended'
  ],
  rules: {
    'import/newline-after-import': 'off',
    'no-param-reassign': 'off',
    'space-before-function-paren': 'off',
    'object-curly-spacing': 'off',
    'block-spacing': 'off',
    'dot-notation': 'off',
    'arrow-parens': 'off',
    'space-before-blocks': 'off',
    'no-confusing-arrow': 'off',
    'arrow-body-style': 'off',
    "vue/no-unused-components": "off",
    semi: 'off'
  },
  parserOptions: {
    parser: 'babel-eslint'
  },
  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        jest: true
      }
    }
  ]
}
