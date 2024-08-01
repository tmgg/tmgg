import {get} from "./obj";
import {expect, test} from 'vitest'


test("get a.b.c == 3", () => {
    const obj = {'a': {'b': {'c': 3}}};
    const value = get(obj, 'a.b.c');
    expect(value).toBe(3)
})