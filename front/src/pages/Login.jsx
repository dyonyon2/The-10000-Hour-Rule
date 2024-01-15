import {Space, Input, Button} from "antd"
import React from "react";

export default function Login(props) {

    const [passwordVisible, setPasswordVisible] = React.useState(false);
    return (
        <Space direction="vertical">
            <h1>Login!</h1>
            <Space direction="horizontal">
                <p>ID : </p>
                <Input.Password placeholder="input" />
                <Button onClick={() => setPasswordVisible((prevState) => !prevState)}>
                    확인
                </Button>
            </Space>
            <Space>
                <p>PW : </p>
                <Input.Password placeholder="input password" visibilityToggle={{
                    visible: passwordVisible,
                    onVisibleChange: setPasswordVisible,
                }}/>
                <Button onClick={() => setPasswordVisible((prevState) => !prevState)}>
                    확인
                </Button>
            </Space>
        </Space>
    );
};