import React from "react";
import { Link , BrowserRouter, Route, useNavigate} from 'react-router-dom';
// import {useHistory} from 'react-router-dom'
import { useState, useEffect } from "react";
import axios from "axios";
import '../css/Profile.css'
import Employee from '../images/login.png'
import Logout from '../images/Logout.png'


function Profile() {

    // const [admin, setAdmin]=useState([])
    // const [loadd, setLoadd]=useState(false)

    // // const history = useHistory("")

    // //GET THE ADMIN
    // useEffect(()=>{
    //     if(loadd===false){
    //         axios.get("http://localhost:8080/admin").then(rs=>{
    //             console.log("AdmiNN",rs.data)
    //             setAdmin(rs.data)
    //             setLoadd(true)
    //     })
    //     .catch(err => {
    //         console.log(err);
    //     })
    // }
// },[admin, loadd]);

// const navigate = useNavigate();
// const handleClick = ()=>{
//     navigate('/login');
// }

    return (
        <div className="div1">
            <button className="logoutbtn" >
                <img className="logoutImg" src={Logout} alt="" />
            </button>
            <div className="profileDiv">
                <img src={Employee} alt="" className="myProfilePhoto"/>
                <h3>Sindi Shima</h3>
            </div>
        </div>
    );
}

export default Profile;
