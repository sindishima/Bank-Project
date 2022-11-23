import React from 'react'
import { Link } from 'react-router-dom';
import '../css/MiniAccounts.css'


const colorList = ['#F7C0FF','#FFC2EE','#F9D0FF','#FDB2FF'] ;
var i = -1 ;

function indexColor(){
    if (i === 3) i=-1 ; 
    i++
    return colorList[i];
}

const colorRect = ['#F4A6FF', '#FFABCF','#EEB5F7','#FF9DE4'] ;
var j = -1 ;

function indexColorRect(){
    if (j === 3) j=-1 ; 
    j++
    return colorRect[j] ;
}

function MiniAccounts(props) {
    const {id, iban, accountStatus, balance, currency, interest} = props;

    return (
        
        <div className='miniacc' style={{background: indexColor()}}>
            <Link to={`/account/${id}`} className="addAccount3">
            <div className='rectangle' style={{background: indexColorRect()}}>
                <h3 className='acc1'>Account {id}</h3>
            </div>
            <h4>{accountStatus}</h4>
            <h4>Balance: {balance}</h4>
            <h4>Currenct: {currency}</h4>
            <h4>IBAN: {iban}</h4>
            </Link>
        </div>
    );
};

export default MiniAccounts