import '../css/watermark.css'
import {sys} from "../common";

const show = false;
let g_text = "机密文件"

// 清除现有的水印
function removeWatermark() {
  const existingWatermarks = document.querySelectorAll('.watermark');
  existingWatermarks.forEach(mark => mark.remove());
}

function createWatermark(text) {
  if (text == null) {
    text = g_text;
  } else {
    g_text = text;
  }
  removeWatermark();

  // 创建新的水印
  const container = document.body;
  const width = window.innerWidth;
  const height = window.innerHeight;
  const yOffset = 150; // Y轴偏移量，用于调整水印之间的垂直间距
  const xOffset = 150; // X轴偏移量，用于调整水印之间的水平间距

  for (let y = 0; y < height; y += yOffset) {
    for (let x = 0; x < width; x += xOffset) {
      const mark = document.createElement('div');
      mark.className = 'watermark';
      mark.textContent = text;
      mark.style.left = x + 'px';
      mark.style.top = y + 'px';
      container.appendChild(mark);
    }
  }
}

// 显示或关闭水印
export function toggleWatermark(path) {
  if (path && path.indexOf('?') > 0) {
    path = path.substring(0, path.indexOf('?'))
  }

  let {watermarkList, name} = sys.getLoginInfo();
  if (watermarkList &&  watermarkList.length > 0 && name) {
    if (watermarkList.indexOf(path) >= 0) {
      createWatermark(name)
    } else {
      removeWatermark()
    }
  }

}

// 监听窗口大小变化事件
window.addEventListener('resize', () => {
  if (show) {
    createWatermark();
  }
});
