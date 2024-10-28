import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    container: {
        height: "100vh",
        alignItems: "center",
        justifyContent: "center",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundColor: "red",
    },

    paper: {
        padding: '4vw',
        alignItems: "center",
        justifyContent: "center",
        width: "30vw",  // 뷰포트 기준 width : vw
        minWidth : "300px",     // 최소 width
        height: "80vh", // 뷰포트 기준 height : vh
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
        backgroundColor: "red",
    },

    red: {
        backgroundColor: "red",
    },

    orange: {
        backgroundColor: "orange",
    },

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
    }

    // logo : {
    //     width: "100px",  // 뷰포트 기준 width : vw
    //     height: "50px", // 뷰포트 기준 height : vh
    //     marginBottom : "30px",
    //     objectFit : "contain",
    //     // color:"black",
    //     backgroundImage : "url(/logo/Logo1.png)",
    //     backgroundRepeat: "no-repeat",
    //     // backgroundSize: 'cover',
    //     backgroundPosition: 'center',
    // },

    
});


export default useStyles;