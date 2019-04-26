import React, { Component } from 'react';
import Draggable from 'react-draggable';
import styles from './FakeClientDevice.scss';

export default class FakeClientDevice extends Component {
  constructor(props) {
    super(props);
    this.width = props.width / 2;
    this.height = props.height / 2;
    this.onClose = props.onClose;
    this.onResize = props.onResize;
  }

  onZoomOut = () => {};
  onZoomIn = () => {};
  onDragStart = (e, draggableData) => {
    const draggableNode = draggableData.node;
    const contentNode = draggableNode.getElementsByClassName(
      styles['FakeClientDevice--content']
    )[0];
    let eventNode = e.target;
    while (eventNode != null) {
      if (eventNode === contentNode) {
        return false;
      } else if (eventNode === draggableNode) {
        return true;
      } else {
        eventNode = eventNode.parentNode;
      }
    }
  };

  render() {
    const borderWidth = 10;
    const containerStyle = {
      width: this.width + borderWidth * 2 + 'px',
      height: this.height + borderWidth * 2 + 'px',
    };
    const contentStyle = {
      width: this.width + 'px',
      height: this.height + 'px',
    };

    return (
      <Draggable
        onStart={(e, data) => {
          return this.onDragStart(e, data);
        }}
      >
        <div className={styles['FakeClientDevice--pad']} style={containerStyle}>
          <div className={styles['FakeClientDevice-controls']}>
            <i
              className="fas fa-search-minus"
              onClick={() => this.onZoomOut()}
            />
            <i className="fas fa-search-plus" onClick={() => this.onZoomIn()} />
            &nbsp;
            <i className="far fa-times-circle" onClick={() => this.onClose()} />
          </div>
          <div
            className={styles['FakeClientDevice--content']}
            style={contentStyle}
          />
        </div>
      </Draggable>
    );
  }
}
