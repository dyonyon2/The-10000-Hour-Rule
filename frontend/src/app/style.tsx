import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    container: {
        alignItems: "center",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
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
    },

    header: {
        minWidth : "800px",
        minHeight : "80px",
        borderBottom: "2px solid gray",
        backgroundColor: "white",
        color:"black",
        position:"fixed",
        zIndex:"1"
    },

    headerMenu :{
        minWidth : "600px",
        width:"30vw",
    },

    navigation :{
        width : "100px",
        top : "80px",
        height:"100vh",
        backgroundColor:"#333333",
        justifyContent: "center",
        flexShrink: 0,
        minHeight:"300px",
        position:"fixed"
    },
    
    mainBody : {
        // backgroundColor:"green",
        marginTop : "80px",
        marginLeft : "100px",
        height:"100vh",
        minHeight:"600px",
        width : "calc(100vw - 100px)",
        flexGrow:1,
        padding : "10px",
    },

    memoBoard : {
        minHeight:"800px",
        // minWidth : "1000px",
        height:"95vh",
        // backgroundImage :"url(/blurMemo.jpg)",
        backgroundImage :"url(/board1.jpg)",
        backgroundSize: "cover", /* 요소의 크기에 맞춰 이미지를 조정 */
        backgroundRepeat : "no-repeat", /* 이미지를 반복하지 않음 */
        backgroundPosition: "center", /* 이미지를 중앙에 위치시킴 */
    },

    memoItem3x3 : {
        width:"25vw",
        height:"25vh",
        minWidth : "150px",
        minHeight : "150px",
        marginTop : "20px",
        marginBottom : "20px",

        padding: "20px",
        backgroundColor: "#fffb87", /* 밝은 노란색으로 포스트잇 느낌 */
        border: "1px solid #e0d800", /* 포스트잇 테두리 */
        borderRadius: "5px", /* 부드러운 모서리 */
        boxShadow: "15px 15px 8px rgba(0, 0, 0, 1)", /* 약간의 그림자 */
        transform: "rotate(-2deg)", /* 살짝 기울여서 포스트잇 느낌 */
        color: "#333" /* 글씨 색상 */
    },

    memoItem4x4 : {
        width:"18vw",
        height:"18vh",
        minWidth : "200px",
        minHeight : "150px",
        marginTop : "20px",
        marginBottom : "20px",
        
        padding: "20px",
        backgroundColor: "#fffb87", /* 밝은 노란색으로 포스트잇 느낌 */
        border: "1px solid #e0d800", /* 포스트잇 테두리 */
        borderRadius: "5px", /* 부드러운 모서리 */
        boxShadow: "15px 15px 8px rgba(0, 0, 0, 1)", /* 약간의 그림자 */
        transform: "rotate(-2deg)", /* 살짝 기울여서 포스트잇 느낌 */
        color: "#333" /* 글씨 색상 */
    },

    memoItem5x5 : {
        width:"16vw",
        height:"14vh",
        minWidth : "200px",
        minHeight : "120px",
        marginTop : "20px",
        marginBottom : "20px",
        
        padding: "20px",
        backgroundColor: "#fffb87", /* 밝은 노란색으로 포스트잇 느낌 */
        border: "1px solid #e0d800", /* 포스트잇 테두리 */
        borderRadius: "5px", /* 부드러운 모서리 */
        boxShadow: "15px 15px 8px rgba(0, 0, 0, 1)", /* 약간의 그림자 */
        transform: "rotate(-2deg)", /* 살짝 기울여서 포스트잇 느낌 */
        color: "#333" /* 글씨 색상 */
    },


    icon : {
        borderRadius: '50%',
        width: '50px',
        height: '60px',
        padding: "10px",
        color: "black",
        marginLeft: "0.25vw",
        marginRight: "0.25vw",
    },

    icon_square : {
        width: '50px',
        // height: '60px',
        padding: "10px",
        color: "black",
        marginLeft: "0.25vw",
        marginRight: "0.25vw",
    },

    nav_icon : {
        borderRadius: '50%',
        width: '100%',
        height: '10vh',
        color: "white",
    },


    margin_TB10 : {
        marginTop : "10px",
        marginBottom : "10px",
    },

    margin_TB : {
        marginTop : "20px",
        marginBottom : "20px",
    },

    margin_LR : {
        marginLeft : "20px",
        marginRight : "20px",
    },

    red: {backgroundColor: "red",},
    orange: {backgroundColor: "orange",},
    white: {backgroundColor: "white",},
    blue: {backgroundColor: "blue",},
    yellow: {backgroundColor: "yellow",},
    black: {backgroundColor: "black",},
    gray: {backgroundColor: "gray",},

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
        minWidth : "200px",     // 최소 width
        minHeight : "70px",    // 최소 height
        backgroundImage : "url(/logo/Logo1.png)",
        backgroundRepeat: "no-repeat",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
    },

});


export default useStyles;