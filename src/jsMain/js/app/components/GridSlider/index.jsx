import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import Draggable from 'react-draggable';
import s from './GridSlider.scss';

const pickerRadius = 12;

const GridSlider = (props) => {
  const _handleDragSlider = (e, data) => {
    props.onChange(e, data);
  };

  const _renderSlider = () => {
    return (
      <Draggable
        onDrag={_handleDragSlider}
        position={{ x: props.x, y: props.y }}
        bounds={{
          top: 0,
          left: 0,
          right: props.width,
          bottom: props.height,
        }}
      >
        <div
          className={s['grid-slider__slider']}
          style={{
            width: pickerRadius * 2,
            height: pickerRadius * 2,
          }}
        />
      </Draggable>
    );
  };

  return (
    <div
      className={classNames(
        props.className,
        s['grid-slider__wrapper']
      )}
      style={{
        width: props.width + pickerRadius * 2 + 4,
        height: props.height + pickerRadius * 2 + 4,
      }}
    >
      {_renderSlider()}
    </div>
  );
};

GridSlider.propTypes = {
  height: PropTypes.number,
  width: PropTypes.number,
  x: PropTypes.number,
  y: PropTypes.number,
  onChange: PropTypes.func,
  className: PropTypes.string,
};

GridSlider.defaultProps = {
  height: 150,
  width: 150,
  x: 150 / 2,
  y: 150 / 2,
  onChange: () => {
    console.warn('You should implement a onChange handler for <GridSlider />');
  },
  className: null,
};

export default GridSlider;
