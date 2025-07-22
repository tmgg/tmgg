# 使用tarojs 

在此基础上，增加了taroify 组件库

- @taroify/core
- @taroify/icons

并修改 babel.config.js
```
plugins: [
    [
      "import",
      {
        libraryName: "@taroify/core",
        libraryDirectory: "",
        style: true,
      },
      "@taroify/core",
    ],
    [
      "import",
      {
        libraryName: "@taroify/icons",
        libraryDirectory: "",
        camel2DashComponentName: false,
        style: () => "@taroify/icons/style",
        customName: (name) => name === "Icon" ? "@taroify/icons/van/VanIcon" : `@taroify/icons/${name}`,
      },
      "@taroify/icons",
    ],
  ],
```

