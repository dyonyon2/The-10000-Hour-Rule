import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    container: {
        height: "100vh",
        // backgroundSize: "cover",
        // backgroundColor: "orange",
        alignItems: "center",
        justifyContent: "center",
        // backgroundRepeat: "no-repeat",
        // backgroundPosition: "center",
        // display: "flex",
        // backgroundImage : "../img/Logo1.png",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
    },

    avatar : {
        width: "100px",  // 뷰포트 기준 width : vw
        height: "100px", // 뷰포트 기준 height : vh
        marginBottom : "30px"
    },

    logo : {
        width: "100px",  // 뷰포트 기준 width : vw
        height: "50px", // 뷰포트 기준 height : vh
        marginBottom : "30px",
        objectFit : "contain",
        // color:"black",
        backgroundImage : "url(/logo/Logo1.png)",
        backgroundRepeat: "no-repeat",
        // backgroundSize: 'cover',
        backgroundPosition: 'center',
    },

    size: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center"
    },

    paper: {
        padding: '4vw',
        display: "flex", // 자식 요소들 쌓는 위치 정리 ,flex는 좌우로 생김, Default는 밑으로 쌓임
        flexDirection: "column", // flexDirect을 좌우가 아닌 위아래로 세팅 -> alignItems, justify가 반전되어 동작함!
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "white",
        width: "30vw",  // 뷰포트 기준 width : vw
        minWidth : "300px",     // 최소 width
        height: "80vh", // 뷰포트 기준 height : vh
        minHeight : "600px",    // 최소 height
        border : "solid",
        borderColor : "black"
    },

    margin: {
        margin: '3px',
    },

    form: {
        marginTop: "30px",
        width: "100%", // Fix IE 11 issue.
    },

    submit: {
        marginTop: "30px",
        marginBottom: "15px",
    }
});


export default useStyles;