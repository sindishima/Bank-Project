import { useNavigate } from 'react-router-dom';

import login from "./images/login.png"

import './css/Login.css'
import { useState } from "react";
import axios from "axios";

const color='white';

function Login(){
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");


    const handleSubmit=(e)=>{
        e.preventDefault();
        const authenticatedPerson={username, password}
        console.log("Authenticated Admin", authenticatedPerson);

        axios.get("http://localhost:8080/login", {params:authenticatedPerson}).then(res=>{
            if (res.data.access_token) {
                localStorage.setItem("user", JSON.stringify(res.data.access_token));
            }

            console.log(res.data.access_token)
            navigate("/account")
        })
    }


    return(
        <div className="login">
            <div className="smalpanel">
                <img src={login} alt="" className="loginImg"/>
                <h2 className="loginText">Login</h2>
                <div className="lowPanel" method="GET">
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="" style={{color: color}} className="usernameText">Username</label><br />
                        <input type="text" className="usernameInput" value={username} onChange={(event) => setUsername(event.target.value)}/><br />
                        <label htmlFor="" style={{color: color}} className="passwordText">Password</label><br />
                        <input type="password" className="passwordInput" value={password} onChange={(event) => setPassword(event.target.value)} /><br />
                        <button type="submit" className="signin" >Sign in</button>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default Login;