import React from "react";
import {Card, Form} from "antd";
import { FieldFileBase64, FieldUploadCropImage} from "@tmgg/tmgg-base";
import {JSEncrypt} from "jsencrypt";

export default class extends React.Component {


    render() {
        const crypt = new JSEncrypt();

        crypt.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTsYZP1fs3tIcOJIEkxWUbyeCZMeLpwRzyjC2Y3qQjhtRwxS04PVlGxi1yIf5lXtWb/GeYNU42FwzbKq2U9cnlMakQnbcqsoFrVPRUMxPTVy1Io+WKDuVi0KqDF6BHdZ0VCWgPxJ1B3DCf/g/UyXgzGitJayLgdwpfww8VKOYeGwIDAQAB");


// Encrypt data
        const originalText = 'Hello, World!';
        const encrypted = crypt.encrypt(originalText);

        console.log('Original:', originalText);
        console.log('Encrypted:', encrypted);
        return <Card title='测试页面'>


          test

        </Card>
    }
}
