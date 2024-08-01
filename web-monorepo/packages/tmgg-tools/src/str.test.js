import {expect, test} from 'vitest'
import {
    capitalize,
    contains,
    count,
    decrypt,
    encrypt,
    getWidth,
    obfuscateString,
    pad,
    reverse,
    subAfter,
    subBefore,
    cutByWidth,
    ellipsis, isStr
} from "./str";


test("str.contains", () => {
    expect(contains("hello", "e")).toBe(true)
    expect(contains("hello", "x")).toBe(false)
    expect(contains("", "x")).toBe(false)
    expect(contains(null, "x")).toBe(false)
    expect(contains(undefined, "x")).toBe(false)
    expect(contains(undefined, "")).toBe(false)
})


test("str.count", () => {
    expect(count("hello", "e")).toBe(1)
    expect(count("hello", "x")).toBe(0)
    expect(count("", "x")).toBe(0)
    expect(count(null, "x")).toBe(0)
    expect(count(undefined, "x")).toBe(0)
    expect(count(undefined, "")).toBe(0)
})


test("str.capitalize", () => {
    expect(capitalize("hello")).toBe("Hello")
    expect(capitalize("")).toBe("")
    expect(capitalize(null)).toBe(null)
    expect(capitalize(undefined)).toBe(undefined)
})

test("str.reverse", () => {
    expect(reverse("hello")).toBe("olleh")
    expect(reverse("")).toBe("")
    expect(reverse(undefined)).toBe(undefined)
})


test("str.subAfter", () => {

    expect(subAfter("baidu.com?name=Jack", "?")).toBe("name=Jack")
    expect(subAfter("moon-cn", ".")).toBe("moon-cn")
    expect(subAfter("moon-cn.cn", "-")).toBe("cn.cn")
    expect(subAfter("moon.cn.cn", ".")).toBe("cn.cn")
    expect(subAfter(null, "?")).toBe(null)
})

test("str.subBefore", () => {
    expect(subBefore("baidu.com?name=Jack", "?")).toBe("baidu.com")
    expect(subBefore("baidu.com", "?")).toBe("baidu.com")
    expect(subBefore(null, "?")).toBe(null)

})

test("str.obfuscateString", () => {
    expect(obfuscateString("abc") !== "abc").toBe(true)
    expect(obfuscateString(null) ).toBe(null)
})

test("str.pad", () => {
    expect(pad("abc", 5)).toBe("00abc")
    expect(pad("abc", 2)).toBe("abc")
    expect(pad(null, 2)).toBe("00")
})

test("str.encrypt", () => {
    expect(encrypt("abc") !== "abc").toBe(true)
})

test("str.decrypt", () => {
    expect(decrypt(encrypt("abc"))).toBe("abc")
})

test("str.getWidth", () => {
    expect(getWidth("abc")).toBe(3)
    expect(getWidth("")).toBe(0)
    expect(getWidth("中国")).toBe(4)
    expect(getWidth("Go中国")).toBe(6)
    expect(getWidth("i中国")).toBe(5)
})

test("str.subByWidth", () => {
    expect(cutByWidth("abc", 2)).toBe("ab")
    expect(cutByWidth("", 2)).toBe("")
    expect(cutByWidth("中国", 2)).toBe("中")
    expect(cutByWidth("Go中国", 2)).toBe("Go")
    expect(cutByWidth("i中国", 2)).toBe("i")
})

test("str.ellipsis", () => {
    expect(ellipsis("abc", 2)).toBe("ab...")
    expect(ellipsis("", 2)).toBe("")
    expect(ellipsis("中国", 2)).toBe("中...")
    expect(ellipsis("Go中国", 2)).toBe("Go...")
    expect(ellipsis("i中国", 2)).toBe("i...")
})


test("str.isStr", () => {
    expect(isStr("abc")).toBe(true)
    expect(isStr("")).toBe(true)
    expect(isStr(null)).toBe(false)
})
