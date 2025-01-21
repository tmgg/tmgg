import React from 'react';
import {Editor as TinyMceEditor} from '@tinymce/tinymce-react';
import {SysUtil} from "../../system";


/**
 * 富文本编辑器
 */

export class FieldEditor extends React.Component {

  render() {

    let serverUrl = SysUtil.getServerUrl();
    let uploadUrl = serverUrl + 'sysFile/upload'
    return <>
      <TinyMceEditor
        initialValue={this.props.value}
        tinymceScriptSrc={serverUrl + "tinymce/tinymce.min.js"}
        init={{
          min_height: 500,
          language: 'zh_CN',
          plugins: [
            'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
            'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
            'insertdatetime', 'media', 'table', 'code', 'help', 'wordcount'
          ],
          toolbar: "fontfamily |fontsize | blocks | bold italic forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat | image | undo redo | help",
          content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }',

          // 上传图片
          images_upload_url: uploadUrl,
          images_upload_base_path: serverUrl

        }}
        onChange={e => {
          if (this.props.onChange) {
            this.props.onChange(e.target.getContent())
          }
        }}
      />
    </>;
  }
}
