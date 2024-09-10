import HttpClient from "./HttpClient";


let USER_INFO = {}
let USER_THEME ="normal-theme"
/**
 * 获取当前用户信息
 */
function getUserInfo(){
  return USER_INFO
}


/**
 * 获取用户信息（从后台加载的，实时的）
 * 并缓存
 */
function loadUserInfo(){
 return  new Promise((resolve, reject) => {
   HttpClient.get("/app/weapp/getUserInfo").then(rs=>{
     _setUserInfo(rs.data)
     resolve(rs)
   }).catch(reject)
 })
}

/**
 * 设置用户信息， 下划线表示需谨慎, 一般是登录时使用
 * @param user
 * @private
 */
function _setUserInfo(user){
  USER_INFO = user
}
function loadUserTheme(){
  return  new Promise((resolve, reject) => {
    HttpClient.get("/app/weapp/theme").then(rs=>{
      _setUserTheme(rs.data)
      resolve(rs)
    }).catch(reject)
  })
}

function _setUserTheme(theme){
  USER_THEME = theme
}

function getUserTheme(){
  return USER_THEME
}
function updateUserTheme(theme){
  return  new Promise((resolve, reject) => {
    HttpClient.post("/app/weapp/theme/save",{theme:theme}).then(rs=>{
      _setUserTheme(rs.data)
      resolve(rs)
    }).catch(reject)
  })
}
export default {
  getUserInfo, loadUserInfo, _setUserInfo,getUserTheme,updateUserTheme,loadUserTheme
}
