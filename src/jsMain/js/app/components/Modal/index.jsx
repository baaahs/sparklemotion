import React, { Fragment } from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import { MODAL_PORTAL_DOM_NODE_ID } from '../../constants';
import styles from './Modal.scss';

const Modal = (props) => {
  if (!props.isOpen) return null;

  return ReactDOM.createPortal(
    <div className={styles['modal__container']}>
      <div className={styles['modal__backdrop']} onClick={props.onClose} />
      <div
        className={classNames(
          styles['modal__wrapper'],
          styles[`modal__wrapper--${props.size}`]
        )}
      >
        {props.displayCloseButton && (
          <button className={styles['close-button']} onClick={props.onClose}>
            <i className="fas fa-times-circle" />
          </button>
        )}
        {props.children}
      </div>
    </div>,
    document.getElementById(MODAL_PORTAL_DOM_NODE_ID)
  );
};

Modal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  children: PropTypes.any,
  onClose: PropTypes.func,
  size: PropTypes.oneOf(['small', 'medium', 'large']),
};

Modal.defaultProps = {
  onClose: () => {},
  size: 'medium',
  displayCloseButton: true,
};

export default Modal;
