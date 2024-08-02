export function getRoutes() {
  const routes = [
    {
      "path": "/",
      "exact": true,
      "component": require('./pages/index.jsx').default
    },
    {
      "path": "/log",
      "exact": true,
      "component": require('./pages/log.jsx').default
    }
  ]
}

