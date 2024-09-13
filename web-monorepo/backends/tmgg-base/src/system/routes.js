import React from "react";

const ROUTES_MAP = {}


export function registerRoutes(routes){
    for (let path in  routes) {
        let element = routes[path]
        ROUTES_MAP[path] = element;
    }
}


export function patchClientRoutesRegistered(routes ) {
    const root = routes[0]
    for (let path in ROUTES_MAP) {
        let element = ROUTES_MAP[path]
        root.children.push({
            id: path,
            parentId: root.id,
            path: path,
            element: React.createElement(element)
        })
    }
}
