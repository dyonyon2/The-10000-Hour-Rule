import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    container: {
        // height: "100vh",
        alignItems: "center",
        // justifyContent: "center",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        // backgroundColor: "red",
    },

    paper: {
        padding: '4vw',
        alignItems: "center",
        justifyContent: "center",
        marginTop: "10vh",
        width: "30vw",  // 뷰포트 기준 width : vw
        minWidth : "500px",     // 최소 width
        minHeight : "600px",    // 최소 height
        border : "solid",
        borderColor : "black",
        color:"black",
        backgroundColor: "white",
        // display: "flex", // 자식 요소들 쌓는 위치 정리 ,flex는 좌우로 생김, Default는 밑으로 쌓임
        // flexDirection: "column", // flexDirect을 좌우가 아닌 위아래로 세팅 -> alignItems, justify가 반전되어 동작함!
    },

    avatar : {
        width: "100px",  // 뷰포트 기준 width : vw
        height: "100px", // 뷰포트 기준 height : vh
        marginBottom : "30px",
        // backgroundColor: "red",
    },

    margin_textField : {
        marginTop : "20px",
        marginBottom : "20px",
        // backgroundColor: "red",
    },

    red: {backgroundColor: "red",},
    orange: {backgroundColor: "orange",},
    white: {backgroundColor: "white",},
    blue: {backgroundColor: "blue",},

    margin: {
        margin: '3px',
    },

    submit: {
        marginTop: "15px",
        marginBottom: "15px",
    },

    divider: {
        borderBottom : "3px solid #ccc",
        backgroundColor: "black",
        width : "100%",
        marginTop: "15px",
        marginBottom: "15px",
    },

    logo : {
        // marginBottom : "30px",
        objectFit : "contain",
        minWidth : "200px",     // 최소 width
        minHeight : "80px",    // 최소 height
        // color:"black",
        backgroundImage : "url(/logo/Logo1.png)",
        backgroundRepeat: "no-repeat",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
    },

    
});


export default useStyles;