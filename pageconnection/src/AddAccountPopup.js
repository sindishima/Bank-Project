import close from './images/Close.png'
import './css/App.css'
function CategoryPopUp1(props) {

    return (props.trigger) ? (
        <div className='categoryPopup'>
            <button className='popupButton' onClick={() => props.setTrigger(false)}>
                <img src={close} alt="" className="closeImage"/>
            </button>
            <div className='writeCategoryName'>{props.children}</div>
            </div>
    ) : "";
}

export default CategoryPopUp1
