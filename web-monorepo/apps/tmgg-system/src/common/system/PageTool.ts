export class PageTool {

  private static TAB_REF = null;


  static setTabRef(ref:any){
      this.TAB_REF = ref;
      console.log("设置TabRef",ref)
  }

  /***
   * @param title
   * @param path
   * @param iframe
   */
  static open(title: string, path: string, iframe= false) {
    if (path == null) {
        throw new Error("参数错误，2个参数都不能为空")
    }
    const param = { name: title, path: path, iframe }

    // @ts-ignore
    this.TAB_REF.openTab(param)
  }

  /***
   */
  static close() {
    // @ts-ignore
    this.TAB_REF?.closeCurrentTab()
  }


}
