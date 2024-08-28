export function encodeHtml(s: string) {
  const REGX_HTML_ENCODE = /"|&|'|<|>|[\x00-\x20]|[\x7F-\xFF]|[\u0100-\u2700]/g;
  return typeof s != 'string'
    ? s
    : s.replace(REGX_HTML_ENCODE, function ($0) {
      let c = $0.charCodeAt(0),
        r = ['&#'];
      c = c == 0x20 ? 0xa0 : c;
      // @ts-ignore
      r.push(c);
      r.push(';');
      return r.join('');
    });
}

export function getQueryParams(url: string) {
  console.log('getQueryParams', url);
  let qParams = {};
  let anchor = document.createElement('a');
  anchor.href = url;
  let qStrings = anchor.search.substring(1);
  let params = qStrings.split('&');
  for (let i = 0; i < params.length; i++) {
    let pair = params[i].split('=');
    // @ts-ignore
    qParams[pair[0]] = decodeURIComponent(pair[1]);
  }
  return qParams;
}

export class tools {
  static getQueryParams = getQueryParams;

  static encodeHtml = encodeHtml;
}
