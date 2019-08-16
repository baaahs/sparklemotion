import React, { Fragment } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import Draggable from 'react-draggable';
import s from './GridSlider.scss';
import { disableScroll, enableScroll } from '../utils';

const pickerRadius = 12;

const GridSlider = (props) => {
  const _handleDragSlider = (e, { x, y }) => {
    const data = {
      x: Math.round(x * (props.maxValue / props.width)),
      y: Math.round(y * (props.maxValue / props.height)),
    };

    props.onChange(e, data);
  };

  const _renderSlider = () => {
    return (
      <Draggable
        onStart={disableScroll}
        onDrag={_handleDragSlider}
        onStop={enableScroll}
        position={{
          x: Math.round(props.x * (props.width / props.maxValue)),
          y: Math.round(props.y * (props.height / props.maxValue)),
        }}
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
      className={classNames(props.className, s['grid-slider__wrapper'])}
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
  minValue: PropTypes.number,
  maxValue: PropTypes.number,
  x: PropTypes.number,
  y: PropTypes.number,
  onChange: PropTypes.func,
  className: PropTypes.string,
};

GridSlider.defaultProps = {
  height: 150,
  width: 150,
  minValue: 0,
  maxValue: 255,
  x: 150 / 2,
  y: 150 / 2,
  onChange: () => {
    console.warn('You should implement a onChange handler for <GridSlider />');
  },
  className: null,
};

export default GridSlider;
